import {Duration, Stack, StackProps} from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigatewayv2 from 'aws-cdk-lib/aws-apigatewayv2';
import {HttpLambdaIntegration} from 'aws-cdk-lib/aws-apigatewayv2-integrations'

export class CardanoBackendStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const lambda_backend = new lambda.Function(this, 'readLambdaHandler', {
      runtime: lambda.Runtime.JAVA_21,
      code: lambda.Code.fromAsset("./lambda/build/libs/app-1.0-SNAPSHOT-all.jar"),
      handler: "com.codigomorsa.app.Cardano::onEvent",
      memorySize: 1024,
      timeout: Duration.seconds(10)
    });

    const httpApi = new apigatewayv2.HttpApi(this, 'CardanoApi');
    const lambdaIntegration = new HttpLambdaIntegration(
        'LambdaIntegration',
        lambda_backend
    );

    httpApi.addRoutes({
      path: '/coins',
      methods: [ apigatewayv2.HttpMethod.GET ],
      integration: lambdaIntegration,
    });
  }
}
