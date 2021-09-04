package dte.employme.board.service;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;

public interface JobBoardService
{
	void addJob(JobBoard jobBoard, Job job);
}