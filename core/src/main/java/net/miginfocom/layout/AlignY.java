package net.miginfocom.layout;

public enum AlignY {
    TOP("top"), CENTER("center"), BOTTOM("bottom"), BASELINE("baseline");

    private final String code; // toString().toLowerCase() is not an alternative because of Locale issues (e.g. tr_TR)

    private AlignY(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
