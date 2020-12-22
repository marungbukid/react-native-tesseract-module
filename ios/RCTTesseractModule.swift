//
//  RCTTesseractModule.swift
//  TesseractModule
//
//  Created by Jan Maru De Guzman on 12/22/20.
//

import Foundation

@objc(RCTTesseractModule)

class RCTTesseractModule: NSObject, RCTBridgeModule, G8TesseractDelegate {
  static func moduleName() -> String! {
    return "TesseractModule";
  }
  
  static func requiresMainQueueSetup() -> Bool {
    return true;
  }
  
  @objc
  func recognize(_ uri: NSString,
                 resolve: RCTPromiseResolveBlock,
                 reject: RCTPromiseRejectBlock) -> Void {
    let tesseract: G8Tesseract? = G8Tesseract(language: "eng")
    tesseract?.delegate = self
    tesseract?.image = UIImage(named: uri as String)!
    tesseract?.recognize()
    resolve(tesseract?.recognizedText)
  }
  
  func shouldCancelImageRecognitionForTesseract(tesseract: G8Tesseract!) -> Bool {
      return false // return true if you need to interrupt tesseract before it finishes
  }
}
