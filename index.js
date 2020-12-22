import { useEffect, useState } from "react";
import { NativeModules, DeviceEventEmitter } from "react-native";

export const LANG_ENGLISH = "eng";

export function useEventListener(eventType, listener) {
  useEffect(() => {
    DeviceEventEmitter.addListener(eventType, listener);
    return () => {
      DeviceEventEmitter.removeListener(eventType, listener);
    };
  });
}

const { TesseractModule } = NativeModules;

export default TesseractModule;