//
//  RCTTesseractModule.m
//  TesseractModule
//
//  Created by Jan Maru De Guzman on 12/22/20.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <TesseractOCR/TesseractOCR.h>

@interface RCT_EXTERN_MODULE(RCTTesseractModule, NSObject)
RCT_EXTERN_METHOD(recognize:(NSString *)uri
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
@end
