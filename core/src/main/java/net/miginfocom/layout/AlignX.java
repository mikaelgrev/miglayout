package net.miginfocom.layout;

public enum AlignX {
    LEADING("leading"), LEFT("left"), CENTER("center"), RIGHT("right"), TRAILING("trailing");

    private final String code; // toString().toLowerCase() is not an alternative because of Locale issues (e.g. tr_TR)

    private AlignX(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
