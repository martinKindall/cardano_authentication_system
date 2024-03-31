import {Duration, Stack, StackProps} from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigatewayv2 from 'aws-cdk-lib/aws-apigatewayv2';
import {HttpLambdaIntegration} from 'aws-cdk-lib/aws-apigatewayv2-integrations'
import {PayloadFormatVersion} from "aws-cdk-lib/aws-apigatewayv2";

export class CardanoBackendStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const lambda_validation = new lambda.Function(this, 'lambdaValidationHandler', {
      runtime: lambda.Runtime.JAVA_21,
      code: lambda.Code.fromAsset("./lambda/target/app.jar"),
      handler: "com.walruscode.cardano.Router::validateSign",
      memorySize: 1024,
      timeout: Duration.seconds(10)
    });

    const lambda_show_content = new lambda.Function(this, 'lambdaContentHandler', {
      runtime: lambda.Runtime.JAVA_21,
      code: lambda.Code.fromAsset("./lambda/target/app.jar"),
      handler: "com.walruscode.cardano.Router::showContent",
      memorySize: 1024,
      timeout: Duration.seconds(10)
    });

    const httpApi = new apigatewayv2.HttpApi(this, 'CardanoApi');

    const lambdaValidationIntegration = new HttpLambdaIntegration(
      'LambdaValidationIntegration',
      lambda_validation,
      { payloadFormatVersion: PayloadFormatVersion.VERSION_2_0}
    );

    const lambdaShowContentIntegration = new HttpLambdaIntegration(
      'LambdaShowContentIntegration',
      lambda_show_content,
      { payloadFormatVersion: PayloadFormatVersion.VERSION_2_0}
  );

    httpApi.addRoutes({
      path: '/coins',
      methods: [ apigatewayv2.HttpMethod.GET ],
      integration: lambdaValidationIntegration,
    });

    httpApi.addRoutes({
      path: '/show',
      methods: [ apigatewayv2.HttpMethod.GET ],
      integration: lambdaShowContentIntegration,
    });
  }
}
