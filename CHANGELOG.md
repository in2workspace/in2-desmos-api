# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
- Retry mechanism for Blockchain Adapter and Context Broker.
- Recover after a failure mechanism for Blockchain Adapter and Context Broker.

## [Released]: v1.0.2
- Fix replication loop

## [Released]: v1.0.1
- Make start date time not required

## [Released]: v1.0.0
- DOME Participant validation
- P2P synchronization

## [Released]: v0.6.0
- Hash intertwining between Blockchain Events.
- Hash intertwining between Transactions.

## [Released]: v0.5.0
- Create Entity subscription to Context Broker.
- Create Event subscription to Blockchain Adapter.
- Publish Entity to Context Broker.
- Publish Event to Blockchain Adapter.
- Support integration with Scorpio as Context Broker.
- Support integration with DigitelTs as Blockchain Adapter.
- Has a Docker image to run the application.
- Has a docker-compose.yml file to run the application with all the required dependencies (Context Broker and Blockchain Adapter).
- Has SonarCloud integration.
- Has GitHub Actions integration.

[release]:
