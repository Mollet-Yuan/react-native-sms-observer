
import { DeviceEventEmitter, NativeModules, Platform } from 'react-native';

const { SmsObserver } = NativeModules;
const EVEN_LISTENER = 'OTP_ARRIVED';

const SmsRetrieverModule = (Platform.OS === "ios") ? {} : {
  getHash: SmsObserver.getHash,
  getPhoneNumber: SmsObserver.getPhoneNumber,
  startSmsRetriever: SmsObserver.startSmsRetriever,
  getServiceSupport: SmsObserver.getServiceSupport,
  addSmsListener: (callback) => DeviceEventEmitter.addListener(EVEN_LISTENER, callback),
  removeSmsListener: () => DeviceEventEmitter.removeAllListeners(EVEN_LISTENER)
};

export default SmsRetrieverModule;
