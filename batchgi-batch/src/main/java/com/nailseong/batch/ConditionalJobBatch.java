package com.nailseong.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ConditionalJobBatch {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job conditionalJob() {
        return jobBuilderFactory.get("conditionalJob")
                .start(firstStep(null))
                    .on(ExitStatus.FAILED.getExitCode())
                    .to(afterFailJob())
                    .on("*")
                    .end()
                .from(firstStep(null))
                    .on("*")
                    .to(afterSuccessJob())
                    .on("*")
                    .end()
                .end()
                .build();
    }

    @Bean
    @JobScope
    public Step firstStep(@Value("#{jobParameters[isFail]}") Boolean isFail) {
        return stepBuilderFactory.get("firstStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("Run first step. isFail -> {}", isFail);
                    if (isFail) {
                        contribution.setExitStatus(ExitStatus.FAILED);
                    }
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step afterFailJob() {
        return stepBuilderFactory.get("afterFailJob")
                .tasklet((contribution, chunkContext) -> {
                    log.info("Run after fail job");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step afterSuccessJob() {
        return stepBuilderFactory.get("afterSuccessJob")
                .tasklet((contribution, chunkContext) -> {
                    log.info("Run after success job");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
