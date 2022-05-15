import React from 'react';
import {Box, Button} from 'native-base';
import {
  startScan,
  stopScan,
  getBluetoothDeviceUuid,
  setBluetoothDeviceUuid,
} from '../services/settings';

export default function Home() {
  return (
    <Box>
      <Button onPress={() => startScan(console.log)}>Start scan</Button>
      <Button onPress={stopScan}>Stop scan</Button>
      <Button onPress={() => console.log(getBluetoothDeviceUuid())}>
        Get bluetooth uuid
      </Button>
      <Button onPress={() => setBluetoothDeviceUuid('Test' + Math.random())}>
        Set bluetooth uuid
      </Button>
    </Box>
  );
}
