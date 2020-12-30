require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-sms-observer"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-sms-observer
                   DESC
  s.homepage     = "https://github.com/Mollet-Yuan/react-native-sms-observer"
  # brief license entry:
  s.license      = "MIT"
  # optional - use expanded license entry instead:
  # s.license    = { :type => "MIT", :file => "LICENSE" }
  s.authors      = { "mollet" => "mollet_yuan@163.com" }
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/Mollet-Yuan/react-native-sms-observer.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,c,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  # ...
  # s.dependency "..."
end

