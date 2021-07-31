package com.bitsoft.st.utils

import org.apache.commons.lang.RandomStringUtils
import org.apache.tomcat.util.http.fileupload.FileUtils
import org.springframework.web.multipart.MultipartFile

class FileService {
    static transactional = false

    String uploadFile(MultipartFile workingFile, String destinationDirectory) {
        String fileName = generateFileName(workingFile)
        InputStream uploadedStream = workingFile.inputStream
        File uploadedPath = new File(destinationDirectory, fileName)
        if (!new File(destinationDirectory).exists()) {
            new File(destinationDirectory).mkdirs()
            def file = new File(destinationDirectory)
            file.setWritable(true, true)
        }
        uploadedPath.withOutputStream { out ->
            out << uploadedStream
        }
        uploadedStream.close()

        return fileName
    }

    def generateFileName(def file) {
        String charset = (('A'..'Z') + ('0'..'9')).join()
        Integer length = 9

        def fileNameAndExtension = file.getOriginalFilename().split("\\.")
        if (fileNameAndExtension.size()) {
            return RandomStringUtils.random(length, charset.toCharArray()) + "." + fileNameAndExtension[fileNameAndExtension.length-1]
        }
        return System.currentTimeMillis()
    }


    Boolean removeResource(String relativePath) {
        File file = new File(relativePath)
        if(file.exists()) {
            return  deleteQuietly(file)
        }
        return false
    }

    static boolean deleteQuietly(File file) {
        if (file == null) {
            return false
        }
        try {
            if (file.isDirectory()) {
                FileUtils.cleanDirectory(file)
            }
        } catch (final Exception ignored) {
        }

        try {
            return file.delete()
        } catch (Exception ignored) {
            return false
        }
    }


   /* String getRelativeURLPattern(String path, String type) {
        int lengthToCut
        switch(type) {
            case NamedConstants.RESOURCE_TYPE.RESOURCE:
                lengthToCut = PathManager.getResourceRoot().length()
                break
        }
        return path.substring(lengthToCut)
    }

    void removeFileFromCloud(String baseUrl, String fileLocation, String type) {
        CloudStorageManager.deleteData(fileLocation, type)
    }

    File downloadFile(String url) {
        File tempPath = new File(SessionManager.getTempFolder(), StringUtil.uuid)
        tempPath.mkdir()
        String fileName = FilenameUtils.getName(url)
        File file = new File(tempPath, fileName)
        file.createNewFile()
        OutputStream outputStream = file.newOutputStream()
        outputStream << new URL(url).openStream()
        outputStream.close()
        return file
    }

    MultipartFile downloadAsMultipartFile(String url) {
        File file = downloadFile(url)
        String contentType = new URL(url).openConnection().contentType
        WebCommanderMultipartFile multipartFile = new WebCommanderMultipartFile(file.name, file.name, contentType, file.newInputStream())
        return multipartFile
    }

    CloudConfig uploadModifiableResource(File file, String relativeDestinationPath) {
        String path = appResource.getCloudCustomRestrictedResourceUrl(relativePath: relativeDestinationPath)
        return CloudStorageManager.uploadPrivateData(file, NamedConstants.CLOUD_CONFIG.SYSTEM_DEFAULT, path)
    }

    CloudConfig uploadModifiableResource(OutputStream fileInputStream, String relativeDestinationPath) {
        return null
    }

    InputStream getModifiableResourceStream(String relativePath) {
        String cloudLocation = appResource.getCloudCustomRestrictedResourceUrl(relativePath: relativePath)
        InputStream inputStream = CloudStorageManager.getDataStream(cloudLocation, NamedConstants.CLOUD_CONFIG.SYSTEM_DEFAULT)
        if(!inputStream) {
            File file = new File("${appResource.getCustomRestrictedResourcePath(relativePath: relativePath)}")
            if (!file.exists()) {
                return null
            }
            inputStream = new FileInputStream(file)
        }
        return inputStream
    }

    InputStream getRestrictedResourceStream(String relativePath) {
        File file = new File("${appResource.getRestrictedResourcePath(relativePath: relativePath)}")
        if (!file.exists()) {
            return null
        }
        InputStream inputStream = new FileInputStream(file)
        return inputStream
    }

    Boolean removeModifiableResource(String relativePath) {
        String cloudLocation = appResource.getCloudCustomRestrictedResourceUrl(relativePath: relativePath)
        Boolean deleted = CloudStorageManager.deleteData(cloudLocation, NamedConstants.CLOUD_CONFIG.SYSTEM_DEFAULT)

        File file = new File("${appResource.getCustomRestrictedResourcePath(relativePath: relativePath)}")
        if(file.exists()) {
            deleted = FileUtil.deleteQuietly(file)
        }
        return deleted
    }

    Boolean removeBulkModifiableResource(String relativePath) {
        String cloudLocation = appResource.getCloudCustomRestrictedResourceUrl(relativePath: relativePath)
        Boolean deleted = CloudStorageManager.deleteBulkData(cloudLocation, NamedConstants.CLOUD_CONFIG.SYSTEM_DEFAULT)

        File file = new File("${appResource.getCustomRestrictedResourcePath(relativePath: relativePath)}")
        if(file.exists()) {
            deleted = FileUtil.deleteQuietly(file)
        }
        return deleted
    }

    Map getSystemDefaultEmailTemplateData(Serializable id) {
        Map data = [:]
        def template = EmailTemplate.get(id)
        def templatePath = AppResourceTagLib.EMAIL_TEMPLATES + File.separator + template.identifier
        data.text = getRestrictedResourceStream(templatePath + File.separator + AppResourceTagLib.DEFAULT_TXT).text
        data.html = getRestrictedResourceStream(templatePath + File.separator + AppResourceTagLib.DEFAULT_HTML).text
        return data
    }

    void putModifiableResource(File source, String relativePath, String cloudResourceType){
        FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_MODIFIABLE_RESOURCE, relativePath).putFileIntoSystem(source, cloudResourceType)
    }

    CloudConfig putModifiableResource(InputStream source, String relativePath, String cloudResourceType){
        FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_MODIFIABLE_RESOURCE, relativePath).putFileIntoSystem(source, cloudResourceType)
    }


    void putPubResource(InputStream source, String relativePath, String cloudResourceType){
        FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_PUB, relativePath).putFileIntoSystem(source, cloudResourceType)
    }


    void putModifiableResource(String source, String relativePath, String cloudResourceType){
        FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_MODIFIABLE_RESOURCE, relativePath).putFileIntoSystem(source, cloudResourceType)
    }

    void putResource(File source, String relativePath, String cloudResourceType){
        FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_RESOURCE, relativePath).putFileIntoSystem(source, cloudResourceType)
    }

    void putResource(InputStream source, String relativePath, String cloudResourceType){
        FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_RESOURCE, relativePath).putFileIntoSystem(source, cloudResourceType)
    }


    void putResource(String source, String relativePath, String cloudResourceType){
        FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_RESOURCE, relativePath).putFileIntoSystem(source, cloudResourceType)
    }

    void removeResource(String relativePath, String cloudResourceType){
        FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_RESOURCE, relativePath).removeFileFromSystem(cloudResourceType)
    }

    void removeModifiableResource(String relativePath, String cloudResourceType){
        FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_MODIFIABLE_RESOURCE, relativePath).removeFileFromSystem(cloudResourceType)
    }

    InputStream readResourceFileContentFromSystem(String relativePath, String cloudResourceType){
       return FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_RESOURCE, relativePath)
               .readFileContentFromSystem(cloudResourceType)
    }

    InputStream readTemplateFileContentFromSystem(String relativePath, String cloudResourceType){
       return FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_TEMPLATE, relativePath)
               .readFileContentFromSystem(cloudResourceType)
    }

    InputStream readModifiableResourceFromSystem(String relativePath, String cloudResourceType){
        return FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_MODIFIABLE_RESOURCE, relativePath)
                .readFileContentFromSystem(cloudResourceType)
    }

    boolean isExistPubResource(String relativePath, String cloudResourceType){
       FileManager fileManager = FileManager.getInstance().setDestinationPath(FileManager.TYPE_PUB, relativePath)
        return fileManager.isExistFile(cloudResourceType)
    }

    boolean isExistTemplateResource(String relativePath){
        FileManager fileManager = FileManager.getInstance().setDestinationPath(FileManager.TYPE_TEMPLATE, relativePath)
        return fileManager.isExistFile(NamedConstants.CLOUD_CONFIG.SYSTEM_DEFAULT)
    }

    boolean isExistResource(String relativePath){
            FileManager fileManager = FileManager.getInstance().setDestinationPath(FileManager.TYPE_RESOURCE, relativePath)
            return fileManager.isExistFile(NamedConstants.CLOUD_CONFIG.DEFAULT)
     }

    void removeProductResource(String relativePath, AbstractStaticResource resource){
        FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_RESOURCE, relativePath).removeFileFromSystemByCloudConfig(resource.cloudConfig)
    }


    void removeProductSpec(String productId, Resource spec){
        String relativePath = appResource.getProductSpecRelativeUrl(productId, spec.resourceName)
        removeProductResource(relativePath, spec)
    }


    CloudConfig putProductResource(InputStream source, String relativePath) {
        return FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_RESOURCE, relativePath).putFileIntoSystem(source, NamedConstants.CLOUD_CONFIG.DEFAULT)
    }

    CloudConfig putProductSpec(InputStream source, Product product) {
        String relativePath = appResource.getProductSpecRelativeUrl(product.id, product.spec.resourceName)
        return putProductResource(source,relativePath)
    }

    CloudConfig putProductDownloadableFile(InputStream source, String relativePath) {
        return putModifiableResource(source, relativePath, NamedConstants.CLOUD_CONFIG.DEFAULT)
    }

    CloudConfig putTemplateDirectory(String  source, String relativePath, String cloudResourceType){
        return FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_TEMPLATE, relativePath).cleanAndMoveDirectory(source, cloudResourceType)
    }

    CloudConfig putResourceDirectory(String  source, String relativePath, String cloudResourceType){
        return FileManager.getInstance()
                .setDestinationPath(FileManager.TYPE_RESOURCE, relativePath).cleanAndMoveDirectory(source, cloudResourceType)
    }*/

}
