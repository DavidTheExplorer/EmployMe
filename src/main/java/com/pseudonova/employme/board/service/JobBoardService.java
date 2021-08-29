package com.pseudonova.employme.board.service;

import com.pseudonova.employme.board.JobBoard;
import com.pseudonova.employme.job.Job;

public interface JobBoardService
{
	void addJob(JobBoard jobBoard, Job job);
}