package vo;

public class SeatDTO {
    private int seatIdx;    // 좌석번호 (PK)
    private int memIdx;     // 앉은 사람 번호 (빈자리면 0 또는 null)
    private int status;     // 0:빈좌석, 1:사용중
    private String memName;
    private int memAge;
    
    public SeatDTO() {};

    public SeatDTO(int seatIdx, int memIdx, int status, String memName, int memAge) {
        this.seatIdx = seatIdx;
        this.memIdx = memIdx;
        this.status = status;
        this.memName = memName;
        this.memAge = memAge;
    }
    public int getMemAge() {
        return memAge;
    }

    public void setMemAge(int memAge) {
        this.memAge = memAge;
    }

    public String getMemName() {
        return memName;
    }

    public void setMemName(String memName) {
        this.memName = memName;
    }

    public int getSeatIdx() {
        return seatIdx;
    }
    public void setSeatIdx(int seatIdx) {
        this.seatIdx = seatIdx;
    }
    public int getMemIdx() {
        return memIdx;
    }
    public void setMemIdx(int memIdx) {
        this.memIdx = memIdx;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}
