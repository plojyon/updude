import {NativeEventEmitter, NativeModules} from 'react-native';

const {LockModule} = NativeModules;

const eventEmitter = new NativeEventEmitter(NativeModules.LockModule);

/**
 * startScan
 * @param {*} callback
 * @returns The event emmiter, to unsubscribe from the event
 */
export function startScan(callback) {
  console.log('adding listener...');
  const eventListener = eventEmitter.addListener(
    'BluetoothScanResult',
    callback,
  );

  console.log(eventListener);
  // TODO: call unsubscribe methods
  // https://reactnative.dev/docs/native-modules-android#sending-events-to-javascript
  LockModule.startScan();
  return eventListener;
}

export function stopScan() {
  eventEmitter.removeAllListeners('BluetoothScanResult');
  return LockModule.stopScan();
}

export function enable() {
  return LockModule.enable();
}

export function disable() {
  return LockModule.disable();
}

export function lock() {
  return LockModule.lock();
}

export function startForegroundService() {
  LockModule.startForegroundService();
}

export function updateSettings(type, value) {
  LockModule.updateSettings(type, value);
}

export function getSettings(callback) {
  LockModule.getSettings(callback);
}

export function startNFCread(callback) {
  console.log('starting NFC read');
  LockModule.startReading();
  const eventListener = eventEmitter.addListener('NFCReadResult', callback);

  console.log(eventListener);
  // TODO: call unsubscribe methods
  // https://reactnative.dev/docs/native-modules-android#sending-events-to-javascript
  LockModule.startReading();
  return eventListener;
}

export function stopNFCread() {
  console.log('stopping NFC read');
  eventEmitter.removeAllListeners('NFCReadResult');
  return LockModule.stopReading();
}
