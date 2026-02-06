package vo;

public class MemberDTO {
    int mem_idx;
    String mem_id;
    String mem_pass;
    String mem_name;
    int mem_time;
    int mem_age;
    int mem_money;
    int mem_admin;

    public MemberDTO() {
    }

    public MemberDTO(String mem_id, String mem_pass, String mem_name, int mem_age) {
        this.mem_id = mem_id;
        this.mem_pass = mem_pass;
        this.mem_name = mem_name;
        this.mem_age = mem_age;
    }

    public int getMem_idx() {
        return this.mem_idx;
    }

    public void setMem_idx(int mem_idx) {
        this.mem_idx = mem_idx;
    }

    public String getMem_id() {
        return this.mem_id;
    }

    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }

    public String getMem_pass() {
        return this.mem_pass;
    }

    public void setMem_pass(String mem_pass) {
        this.mem_pass = mem_pass;
    }

    public String getMem_name() {
        return this.mem_name;
    }

    public void setMem_name(String mem_name) {
        this.mem_name = mem_name;
    }

    public int getMem_time() {
        return this.mem_time;
    }

    public void setMem_time(int mem_time) {
        this.mem_time = mem_time;
    }

    public int getMem_age() {
        return this.mem_age;
    }

    public void setMem_age(int mem_age) {
        this.mem_age = mem_age;
    }

    public int getMem_money() {
        return this.mem_money;
    }

    public void setMem_money(int mem_money) {
        this.mem_money = mem_money;
    }

    public int getMem_admin() {
        return this.mem_admin;
    }

    public void setMem_admin(int mem_admin) {
        this.mem_admin = mem_admin;
    }
    
}