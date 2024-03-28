#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { CardanoBackendStack } from '../lib/cardano_backend-stack';

const app = new cdk.App();
new CardanoBackendStack(app, 'CardanoBackendStack');
