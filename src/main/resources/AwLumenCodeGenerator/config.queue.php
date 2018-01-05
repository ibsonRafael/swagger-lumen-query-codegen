<?php

return [
    /*
    |--------------------------------------------------------------------------
    | Default Queue Driver
    |--------------------------------------------------------------------------
    */
    'default' => env('QUEUE_DRIVER', 'beanstalkd'),

    /*
    |--------------------------------------------------------------------------
    | Queue Connections
    |--------------------------------------------------------------------------
    */

    'connections' => [
        'beanstalkd' => [
            'driver' => 'beanstalkd',
            'host'   => 'beanstalk.application.dev',
            'queue'  => 'default',
            'ttr'    => 3600,
        ]
    ],

    /*
    |--------------------------------------------------------------------------
    | Failed Queue Jobs
    |--------------------------------------------------------------------------
    */
    'failed' => [
        'database' => env('DB_CONNECTION', 'mysql'),
        'table'    => env('QUEUE_FAILED_TABLE', 'failed_jobs'),
    ],

];
