#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { CardanoBackendStack } from '../lib/cardano_backend-stack';

const app = new cdk.App();
new CardanoBackendStack(app, 'CardanoBackendStack', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: 'eu-central-1'
}});
