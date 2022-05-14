import {NativeModules} from 'react-native';

const {LockModule} = NativeModules;

export function enable() {
  return LockModule.enable();
}

export function disable() {
  return LockModule.disable();
}

export function lock() {
  return LockModule.lock();
}
