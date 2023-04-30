package net.miginfocom.layout;

/**
 * NORMAL: Bounds will be calculated as if the component was visible.<br>
 * SIZE_0_RETAIN_GAPS: If hidden the size will be 0, 0 but the gaps remain.<br>
 * SIZE_0_GAPS_0: If hidden the size will be 0, 0 and gaps set to zero.<br>
 * DISREGARD: If hidden the component will be disregarded completely and not take up a cell in the grid..
 */
public enum HideMode {
    NORMAL(0), SIZE_0_RETAIN_GAPS(1), SIZE_0_GAPS_0(2), DISREGARD(3);

    private final int code;

    private HideMode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    static public HideMode of(int code) {
        for (HideMode hideMode : values()) {
            if (hideMode.code == code) {
                return hideMode;
            }
        }
        throw new IllegalArgumentException("Code does not exist " + code);
    }
}