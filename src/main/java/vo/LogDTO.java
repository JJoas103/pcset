package vo;
import java.sql.Timestamp;

public class LogDTO {

    private int logIdx;         // 로그번호 (PK)
    private int logType;        // 1:충전, 2:구매
    private int memIdx;         // 회원번호
    private int logAmount;      // 금액
    private Timestamp logDate;  // 발생시간 (DB의 datetime 대응)
    
    private String memName;
    
    public LogDTO() {}
    
    public LogDTO(int logIdx, int logType, int memIdx, int logAmount, Timestamp logDate) {
        this.logIdx = logIdx;
        this.logType = logType;
        this.memIdx = memIdx;
        this.logAmount = logAmount;
        this.logDate = logDate;
    }
    public String getMemName() {
        return memName;
    }

    public void setMemName(String memName) {
        this.memName = memName;
    }

    public int getLogIdx() {
        return logIdx;
    }

    public void setLogIdx(int logIdx) {
        this.logIdx = logIdx;
    }

    public int getLogType() {
        return logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
    }

    public int getMemIdx() {
        return memIdx;
    }

    public void setMemIdx(int memIdx) {
        this.memIdx = memIdx;
    }

    public int getLogAmount() {
        return logAmount;
    }

    public void setLogAmount(int logAmount) {
        this.logAmount = logAmount;
    }

    public Timestamp getLogDate() {
        return logDate;
    }

    public void setLogDate(Timestamp logDate) {
        this.logDate = logDate;
    }
}
