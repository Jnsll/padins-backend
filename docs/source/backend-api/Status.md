---
layout: default
id: Status
title: Backend API
parent: backend-api
---
# Documentation

## `public class Status`

The status class stores information about the status of a specific Flow or Group (so one per network)

Created by antoine on 02/06/17.

## `public long getStartedTime ()`

Give the time when the network first started

 * **Returns:** The timestamp of the moment when the network first started

## `public long getStoppedTime ()`

Give the time when the network stopped the last time

 * **Returns:** The timestamp of the moment when the network stopped

## `public void start ()`

Tell the Status instance that the network started.

## `public void stop ()`

Tell the Status instance that the network stopped.

## `public long getUptime ()`

Gives the total running duration of the network.

 * **Returns:** The total running duration of the network.

## `public boolean hasStarted ()`

Tell whether the network ever started or not.

 * **Returns:** true if the network has ever started.

## `public boolean isRunning ()`

Tell whether the network is currently running.

 * **Returns:** true if running

## `public boolean isInDebugMode ()`

Tell whether the network is in debug mode or not

 * **Returns:** if in debug mode

## `public void turnDebugOn ()`

Turn debug on only for status, don't do anything on the node or network

## `public void turnDebugOff ()`

Turn debug off only for status, don't do anything on the node or network
