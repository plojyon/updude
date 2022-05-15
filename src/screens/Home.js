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
    <Box p="5">
      <Button mt={5} onPress={() => enable()}>Enable admin</Button>
      <Button mt={5} onPress={() => startScan(console.log)}>Start scan</Button>
      <Button mt={5} onPress={stopScan}>Stop scan</Button>
      <Button mt={5} onPress={() => getSettings(console.log)}>
        Get bluetooth uuid
      </Button>
      <Button mt={5} onPress={() => updateSettings('bluetooth', 'B8:72:EB:5B:F6:03')}>
        Set bluetooth uuid
      </Button>
      <Button mt={5} onPress={() => startForegroundService()}>
        ACTIVATE
      </Button>
    </Box>
  );
}
