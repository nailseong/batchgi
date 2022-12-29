package com.nailseong.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobLoggerListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance()
                .getJobName();
        Long jobId = jobExecution.getJobId();
        log.info("Start job execution {} - {}", jobName, jobId);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance()
                .getJobName();
        ExitStatus exitStatus = jobExecution.getExitStatus();
        log.info("Complete job execution {} - {}", jobName, exitStatus.getExitCode());
    }
}
