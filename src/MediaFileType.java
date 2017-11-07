 enum MediaFileType {
    MP3, WAV, AIIF, AIF, FXM, FLV;

    @Override
    public String toString() {
        return "."+this.name().toLowerCase();
    }
    public static String[] getExtension(){
        String[] extensionMediaArray=new String[MediaFileType.values().length];
        int i=0;
        for (MediaFileType type : MediaFileType.values()){
            extensionMediaArray[i++]="*."+type.name();
        }
        return extensionMediaArray;
    }


}
