declare module "react-native-tesseract-module" {
  export interface TesseractModule {
    recognize(uri: string): Promise<String>;
  }

  const TesseractModule: TesseractModule;

  export default TesseractModule;
}