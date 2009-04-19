<?php
// +----------------------------------------------------------------------+
// | WR Time Tracker
// +----------------------------------------------------------------------+
// | Copyright (c) 2004-2006 WR Consulting (http://wrconsulting.com)
// +----------------------------------------------------------------------+
// | LIBERAL FREEWARE LICENSE: This source code document may be used
// | by anyone for any purpose, and freely redistributed alone or in
// | combination with other software, provided that the license is obeyed.
// |
// | There are only two ways to violate the license:
// |
// | 1. To redistribute this code in source form, with the copyright
// |    notice or license removed or altered. (Distributing in compiled
// |    forms without embedded copyright notices is permitted).
// |
// | 2. To redistribute modified versions of this code in *any* form
// |    that bears insufficient indications that the modifications are
// |    not the work of the original author(s).
// |
// | This license applies to this document only, not any other software
// | that it may be combined with.
// |
// +----------------------------------------------------------------------+
// | Contributors:  Igor Melnik <imelnik@wrconsulting.com>
// +----------------------------------------------------------------------+

	require_once('initialize.php');
	import('UserHelper');
	import('TimeHelper');
	import('DateTime');
	import('SysConfig');
	import('ProjectHelper');
	import('ActivityHelper');

	function doStart($user, $cl_project, $cl_activity, $cl_date, $cl_start) {
		$crdate = new DateTime('m/d/Y', $cl_date);
		TimeHelper::insert(
			$crdate->toString(DB_DATEFORMAT),
			$user->getActiveUser(),
			$cl_project,
			$cl_activity,
			$cl_start,
			"",
			"",
			"",
			"",
			true);
	}
	
	function doFinish($user, $cl_project, $cl_activity, $cl_date, $cl_finish, $rec) {
		if (TimeHelper::toMinutes($cl_finish)==TimeHelper::toMinutes($rec["tfrom"])) {
			doDelete($user, $rec);
		} else {
			$crdate = new DateTime();
			$crdate->parseVal($rec["al_date"], DB_DATEFORMAT);
			
			$locktime = $user->getLocktime();
			$lockdate = $crdate->getClone();
			if ($locktime<0 || $locktime==null || $locktime=="") {
				$sc = new SysConfig(new User($user->getOwnerId(), false));
				$locktime = $sc->getValue(SYSC_LOCK_DAYS);	
			}
			if ($locktime>0) {
				$lockdate = new DateTime();
				$lockdate->decDay($locktime);
			}
			
			if ($crdate->before($lockdate)) {
				exit;
			}
			
			if (!TimeHelper::isValidTime($cl_finish)) {
	      		exit;
	    	}
	
			TimeHelper::update(
				$rec["al_date"],
				$rec["al_timestamp"],
				$user->getActiveUser(),
				$rec["al_project_id"],
				$rec["al_activity_id"],
				$rec["tfrom"],
				$cl_finish,
				"",
				$rec["al_comment"],
				true);
		}
	}
	
	function doDelete($user, $rec) {
		TimeHelper::delete($user->getActiveUser(), $rec["al_timestamp"], $rec["al_date"]);
	}
	
	$cl_login		= $request->getParameter('login');
	$cl_password	= $request->getParameter('password');
	
	$auth->doLogin($cl_login, $cl_password);
	if ($auth->isAuthenticated()) {
		$user = new User($auth->getUserId());
		if ($user->isAdministrator()) {
			exit();
		}
	} else {
		exit();
	}
	
	$crdate = new DateTime();

	$rec = TimeHelper::checkPresentTimeWithEmptyDuration($user->getUserId());
	
	$cl_project		= $request->getParameter('project');
	$cl_activity	= $request->getParameter('activity');
	$cl_action		= $request->getParameter('action');
	$cl_start		= $request->getParameter('start');
	$cl_finish		= $request->getParameter('finish');
	$cl_date		= $request->getParameter('date');
	
	if ($cl_action=="start") {
		if ($rec) {
			doFinish($user, $cl_project, $cl_activity, $cl_date, $cl_finish, $rec);
		}
		doStart($user, $cl_project, $cl_activity, $cl_date, $cl_start);
		$rec = TimeHelper::checkPresentTimeWithEmptyDuration($user->getUserId());
	}
	
	if ($cl_action=="stop" && $rec) {
		doFinish($user, $cl_project, $cl_activity, $cl_date, $cl_finish, $rec);
		$rec = TimeHelper::checkPresentTimeWithEmptyDuration($user->getUserId());
	}
	
	$project_list = ProjectHelper::findAllProjects($user, true);
	$activity_list = ActivityHelper::findAllActivity($user);

	$week_time = TimeHelper::getTimePerWeek($user, $crdate);
	$daily_time = TimeHelper::getTotalTime($crdate->toString(DB_DATEFORMAT),$user->getActiveUser());
	
	header('Content-Type: text/xml');

	$xml = '<?xml version="1.0" encoding="UTF-8"?>';
	$xml = '<info>';
	$xml .= '<projects>';
	foreach ($project_list as $project) {
		$xml .= '<project id="'.$project["p_id"].'"><![CDATA['.$project["p_name"].']]></project>';
	}
	$xml .= '</projects>';
	$xml .= '<activities>';
	foreach ($activity_list as $activity) {
		$res = array();
		foreach ($activity["aprojects"] as $project) $res[] = $project["p_id"];
		$xml .= '<activity id="'.$activity["a_id"].'" project="'.join(",",$res).'"><![CDATA['.$activity["a_name"].']]></activity>';
	}
	$xml .= '</activities>';
	$xml .= '<records>';
	if ($rec) {
		$xml .= '<incompleate project="'.$rec["al_project_id"].'" activity="'.$rec["al_activity_id"].'" start="'.$rec["tfrom"].'" finish="" date="'.$rec["al_date"].'"/>';
	}
	$xml .= '<compleate count="0" daily_time="'.$daily_time.'" week_time="'.$week_time.'"/>';
	$xml .= '</records>';
	$xml .= '</info>';
	
	print $xml;
?>