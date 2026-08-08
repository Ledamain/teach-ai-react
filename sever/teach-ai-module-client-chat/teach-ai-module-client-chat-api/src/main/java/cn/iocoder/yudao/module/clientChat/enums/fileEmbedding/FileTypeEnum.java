package cn.iocoder.teach-ai.module.clientChat.enums.fileEmbedding;

public enum FileTypeEnum {
    TEMP("temp"),
    PERMANENT("permanent");

    public final String type;

    FileTypeEnum(String type) {
        this.type = type;
    }
}
