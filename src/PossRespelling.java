public class PossRespelling {
    private int position;
    private String sessionId;

    public PossRespelling(int position, String sessionId) {
        this.position = position;
        this.sessionId = sessionId;
    }

    public int getPosition() {
        return position;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
