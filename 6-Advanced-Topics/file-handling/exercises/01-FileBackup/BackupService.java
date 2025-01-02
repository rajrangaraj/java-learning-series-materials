/**
 * Service for managing file backups with versioning
 */
public class BackupService {
    private final Path sourceDir;
    private final Path backupDir;
    private final BackupConfig config;
    private final BackupStats stats;
    
    public BackupService(Path sourceDir, Path backupDir, BackupConfig config) {
        this.sourceDir = sourceDir;
        this.backupDir = backupDir;
        this.config = config;
        this.stats = new BackupStats();
    }
    
    /**
     * Creates a new backup of the source directory
     */
    public void createBackup() throws IOException {
        // Create backup directory if it doesn't exist
        Files.createDirectories(backupDir);
        
        // Create version directory with timestamp
        String timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path versionDir = backupDir.resolve(timestamp);
        Files.createDirectory(versionDir);
        
        // Copy files
        try {
            Files.walk(sourceDir)
                 .filter(path -> !Files.isDirectory(path))
                 .forEach(source -> {
                     try {
                         Path relative = sourceDir.relativize(source);
                         Path target = versionDir.resolve(relative);
                         
                         // Create parent directories if needed
                         Files.createDirectories(target.getParent());
                         
                         // Check if file should be backed up
                         if (shouldBackupFile(source, relative)) {
                             Files.copy(source, target);
                             stats.recordBackedUpFile(Files.size(source));
                         } else {
                             stats.recordSkippedFile();
                         }
                     } catch (IOException e) {
                         stats.recordFailedFile();
                         System.err.println("Failed to backup: " + source);
                         e.printStackTrace();
                     }
                 });
            
            // Clean up old backups if needed
            cleanupOldBackups();
            
        } catch (IOException e) {
            throw new IOException("Backup failed", e);
        }
    }
    
    /**
     * Restores files from a specific backup version
     */
    public void restoreBackup(String version, Path targetDir) throws IOException {
        Path versionDir = backupDir.resolve(version);
        if (!Files.exists(versionDir)) {
            throw new IllegalArgumentException("Backup version does not exist: " + version);
        }
        
        // Create target directory if it doesn't exist
        Files.createDirectories(targetDir);
        
        // Copy files from backup
        Files.walk(versionDir)
             .filter(path -> !Files.isDirectory(path))
             .forEach(source -> {
                 try {
                     Path relative = versionDir.relativize(source);
                     Path target = targetDir.resolve(relative);
                     
                     Files.createDirectories(target.getParent());
                     Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                     stats.recordRestoredFile(Files.size(source));
                 } catch (IOException e) {
                     stats.recordFailedRestore();
                     System.err.println("Failed to restore: " + source);
                     e.printStackTrace();
                 }
             });
    }
    
    /**
     * Lists available backup versions
     */
    public List<BackupVersion> listBackupVersions() throws IOException {
        if (!Files.exists(backupDir)) {
            return Collections.emptyList();
        }
        
        return Files.list(backupDir)
                   .filter(Files::isDirectory)
                   .map(path -> {
                       try {
                           return new BackupVersion(
                               path.getFileName().toString(),
                               Files.getLastModifiedTime(path).toInstant(),
                               calculateBackupSize(path)
                           );
                       } catch (IOException e) {
                           return null;
                       }
                   })
                   .filter(Objects::nonNull)
                   .sorted(Comparator.comparing(BackupVersion::getTimestamp).reversed())
                   .collect(Collectors.toList());
    }
    
    private boolean shouldBackupFile(Path file, Path relative) throws IOException {
        // Check file extension
        if (!config.getIncludedExtensions().isEmpty() &&
            !config.getIncludedExtensions().contains(getExtension(file))) {
            return false;
        }
        
        // Check file size
        long size = Files.size(file);
        if (size > config.getMaxFileSize()) {
            return false;
        }
        
        // Check modification time if incremental backup
        if (config.isIncrementalBackup()) {
            BackupVersion lastVersion = getLastBackupVersion();
            if (lastVersion != null) {
                Path lastBackupFile = backupDir.resolve(lastVersion.getVersion())
                                             .resolve(relative);
                if (Files.exists(lastBackupFile) &&
                    Files.getLastModifiedTime(file).equals(
                        Files.getLastModifiedTime(lastBackupFile))) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private void cleanupOldBackups() throws IOException {
        if (config.getMaxVersions() <= 0) {
            return;
        }
        
        List<BackupVersion> versions = listBackupVersions();
        if (versions.size() > config.getMaxVersions()) {
            for (int i = config.getMaxVersions(); i < versions.size(); i++) {
                Path versionDir = backupDir.resolve(versions.get(i).getVersion());
                deleteDirectory(versionDir);
                stats.recordDeletedVersion();
            }
        }
    }
    
    private BackupVersion getLastBackupVersion() throws IOException {
        List<BackupVersion> versions = listBackupVersions();
        return versions.isEmpty() ? null : versions.get(0);
    }
    
    private long calculateBackupSize(Path versionDir) throws IOException {
        return Files.walk(versionDir)
                   .filter(p -> !Files.isDirectory(p))
                   .mapToLong(p -> {
                       try {
                           return Files.size(p);
                       } catch (IOException e) {
                           return 0L;
                       }
                   })
                   .sum();
    }
    
    private String getExtension(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot + 1) : "";
    }
    
    private void deleteDirectory(Path dir) throws IOException {
        Files.walk(dir)
             .sorted(Comparator.reverseOrder())
             .forEach(path -> {
                 try {
                     Files.delete(path);
                 } catch (IOException e) {
                     System.err.println("Failed to delete: " + path);
                 }
             });
    }
    
    public BackupStats getStats() {
        return stats;
    }
} 