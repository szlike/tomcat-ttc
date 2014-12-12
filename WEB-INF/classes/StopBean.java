/*
 * Represents a Stop in the system
 */

public class StopBean implements java.io.Serializable {

    private String routeid;
    private String route_title;
    private String runid;
    private String run_title;
    private String stopid;
    private String stop_title;
    private int user_id;

    public StopBean(){
        setRouteid("");
        setRoute_title("");
        setRunid("");
        setRun_title("");
        setStopid("");
        setStop_title("");
        setUserId(0);
    }

    /**
     * @param id the stop routeid to set
    */
    public void setRouteid(String routeid){
        this.routeid = routeid;
    }

    /**
     * @return the stop routeid
     */
    public String getRouteid(){
        return this.routeid;
    }

    /**
     * @param id the stop route_title to set
    */
    public void setRoute_title(String route_title){
        this.route_title = route_title;
    }

    /**
     * @return the stop route_title
     */
    public String getRoute_title(){
        return this.route_title;
    }

    /**
     * @param id the stop runid to set
    */
    public void setRunid(String runid){
        this.runid = runid;
    }

    /**
     * @return the stop runid
     */
    public String getRunid(){
        return this.runid;
    }

    /**
     * @param id the stop run_title to set
    */
    public void setRun_title(String run_title){
        this.run_title = run_title;
    }

    /**
     * @return the stop run_title
     */
    public String getRun_title(){
        return this.run_title;
    }

    /**
     * @param id the stop stopid to set
    */
    public void setStopid(String stopid){
        this.stopid = stopid;
    }

    /**
     * @return the stop stopid
     */
    public String getStopid(){
        return this.stopid;
    }

    /**
     * @param id the stop stop_title to set
    */
    public void setStop_title(String stop_title){
        this.stop_title = stop_title;
    }

    /**
     * @return the stop stop_title
     */
    public String getStop_title(){
        return this.stop_title;
    }

    /**
     * @return the User user_id
     */
    public int getUserId() {
        return this.user_id;
    }

    /**
     * @param id the User user_id to set
    */
    public void setUserId(int user_id) {
        this.user_id = user_id;
    }
}