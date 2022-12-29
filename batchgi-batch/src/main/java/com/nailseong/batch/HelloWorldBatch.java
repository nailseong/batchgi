package com.nailseong.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HelloWorldBatch {

    private static final String NAME = "name";
    
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job helloWorldJob() {
        return jobBuilderFactory.get("helloWorldJob")
                .incrementer(new RunIdIncrementer())
                .start(greetingStep())
                .next(helloWorldStep())
                .validator(validator())
                .build();
    }

    @Bean
    public Step greetingStep() {
        return stepBuilderFactory.get("greetingStep")
                .tasklet(greetingTasklet(null))
                .build();
    }

    @Bean
    @StepScope
    public Tasklet greetingTasklet(@Value("#{jobParameters[name]}") String name) {
        return (contribution, chunkContext) -> {
            System.out.println(name + " 안녕~~");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step helloWorldStep() {
        return stepBuilderFactory.get("helloWorldStep")
                .tasklet(batchgiTasklet())
                .build();
    }

    @Bean
    public Tasklet batchgiTasklet() {
        return (contribution, chunkContext) -> {
            String name = (String) chunkContext.getStepContext()
                    .getJobParameters()
                    .get("name");
            System.out.println(name + "의 배치기!!!");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public JobParametersValidator validator() {
        DefaultJobParametersValidator validator = new DefaultJobParametersValidator();
        validator.setRequiredKeys(new String[]{NAME});
        return validator;
    }
}
