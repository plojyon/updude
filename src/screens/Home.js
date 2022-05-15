import React from 'react';
import {Box, Button} from 'native-base';
import {
  startScan,
  stopScan,
  getSettings,
  updateSettings, startForegroundService, enable,
} from "../services/settings";

export default function Home() {
  return (
    <Box>
      <Button onPress={() => enable()}>Enable admin</Button>
      <Button onPress={() => startScan(console.log)}>Start scan</Button>
      <Button onPress={stopScan}>Stop scan</Button>
      <Button onPress={() => getSettings(console.log)}>
        Get bluetooth uuid
      </Button>
      <Button onPress={() => updateSettings('bluetooth', 'B8:72:EB:5B:F6:03')}>
        Set bluetooth uuid
      </Button>
      <Button onPress={() => startForegroundService()}>
        ACTIVATE
      </Button>
    </Box>
  );
}
