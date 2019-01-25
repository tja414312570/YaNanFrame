import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.util.quartz.Cron;

/**
 * 此方式不能使用代理
 * @author yanan
 *
 */
@Cron("*/5 * * * * ?")
@Register
public class TestCornClass implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println(new Date()+"执行一次");
	}

}
