package io.github.jiashunx.masker.json.server.type;

/**
 * @author jiashunx
 */
public enum ServerStatus {

    NOT_START("0"),
    START_FAILED("1"),
    START_SUCCESS("2");

    private final String code;

    ServerStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
