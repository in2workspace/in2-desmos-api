# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
- Retry mechanism for Blockchain Adapter and Context Broker.
- Recover after a failure mechanism for Blockchain Adapter and Context Broker.

## [Released]: v1.0.7
- Add M2M authorization with LEARCredentialMachine.

## [Released]: v1.0.6
- Fix slow synchronization

## [Released]: v1.0.5
- Check empty lifecycleStatus in the Global Policies.

## [Released]: v1.0.4
- Implemented Global Policies to validate lifecycleStatus and validFor during entity replication and synchronization.

## [Released]: v1.0.3
- Modified the entity endpoint to return the root object along with its sub-objects.
- Adjust the schedule for the Trusted Access Nodes List task to run every 5 minutes.

## [Released]: v1.0.2
- Change the update entity to allow adding new attributes to the entity.

## [Released]: v1.0.1
- Set startDateTime as non required

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
