require "json"

Pod::Spec.new do |s|
  # NPM package specification
  package = JSON.parse(File.read(File.join(File.dirname(__FILE__), "package.json")))

  s.name         = "TesseractModule"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = "https://github.com/marungbukid/react-native-tesseract-module"
  s.license      = "MIT"
  s.author       = { package["author"]["name"] => package["author"]["email"] }
  s.platforms    = { :ios => "10.0", :tvos => "10.0" }
  s.source       = { :git => "https://github.com/marungbukid/react-native-tesseract-module", :tag => "#{s.version}" }
  s.source_files = "ios/**/*.{h,m}"

  s.dependency "React-Core"
  s.dependency "TesseractOCRiOS"

end
