<?php
define('RESULT_CODE', 'result_code');
define('RESULT_TEXT', 'result_text');
define('RESULT_DATA', 'result_data');

define('ERROR_CODE_SUCCESS', 0);
define('ERROR_CODE_INVALID_ACTION', 1);
define('ERROR_CODE_PARAM_MISSING', 2);

define('PARAM_EVENT_CODE', 'event_code');
define('PARAM_CURRENT_POINTS', 'current_points');

error_reporting(E_ERROR | E_PARSE);
header('Content-type: application/json; charset=utf-8');

header('Expires: Sun, 01 Jan 2014 00:00:00 GMT');
header('Cache-Control: no-store, no-cache, must-revalidate');
header('Cache-Control: post-check=0, pre-check=0', FALSE);
header('Pragma: no-cache');

$response = array();

$request = explode('/', trim($_SERVER['PATH_INFO'], '/'));
$action = array_shift($request);
$id = array_shift($request);

switch ($action) {
    case 'doAttendEvent':
        $eventCode = $_GET[PARAM_EVENT_CODE];
        if (!isset($eventCode)) {
            $response[RESULT_CODE] = ERROR_CODE_PARAM_MISSING;
            $response[RESULT_TEXT] = 'Required parameter(s) missing.';
            $response[RESULT_DATA] = NULL;
        } else {
            switch ($eventCode) {
                case '0001':
                    $points = 100;
                    break;
                case '0002':
                    $points = 200;
                    break;
                default:
                    $points = 0;
                    break;
            }
            $response[RESULT_CODE] = ERROR_CODE_SUCCESS;
            $response[RESULT_TEXT] = 'Success.';
            $resultData = array();
            $resultData[PARAM_CURRENT_POINTS] = $points;
            $response[RESULT_DATA] = $resultData;
        }
        break;
    default:
        $response[RESULT_CODE] = ERROR_CODE_INVALID_ACTION;
        $response[RESULT_TEXT] = 'No action specified or invalid action.';
        $response[RESULT_DATA] = NULL;
        break;
}

echo json_encode($response);
exit();