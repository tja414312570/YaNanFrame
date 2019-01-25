import java.util.Date;
import java.util.List;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.util.quartz.Cron;
import com.YaNan.frame.util.quartz.QuartzManager;

/**
 * 此方式使用代理
 * @author yanan
 *
 */
@Register
public class testCornMethod {
	
	@Cron("*/5 * * * * ?")
	public void init(@Service Log log) {
		log.debug(new Date()+"执行一次第二个");
		log.debug("我："+this);
	}
	public static void getAllJobs(){
        try {
            Scheduler scheduler = QuartzManager.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();
                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    Date nextFireTime = triggers.get(0).getNextFireTime();
                    System.out.println("[jobName] : " + jobName + " [groupName] : "
                        + jobGroup + " - " + nextFireTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
