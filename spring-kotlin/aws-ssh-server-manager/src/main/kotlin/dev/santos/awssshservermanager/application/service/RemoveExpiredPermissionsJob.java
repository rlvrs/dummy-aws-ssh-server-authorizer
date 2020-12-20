package dev.santos.awssshservermanager.application.service;

import org.jobrunr.jobs.lambdas.JobLambda;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoveExpiredPermissionsJob {
  @Autowired
  private JobScheduler jobScheduler;

  @Autowired
  RemoveExpiredPermissionsJobConfiguration config;

  @Autowired
  RemoveExpiredPermissionsService service;

  public void run() {
    BackgroundJob
      .scheduleRecurrently(
        jobLambda(),
        config.removeExpiredPermissionsJobConfig().getCron()
      );
  }

  private JobLambda jobLambda() {
    return service::removeExpiredPermissions;
  }
}
