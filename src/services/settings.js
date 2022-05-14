import {NativeModules} from 'react-native';

const {LockModule} = NativeModules;

export function hello() {
  return LockModule.hello();
}
