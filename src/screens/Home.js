import React from 'react';
import {Box, Button} from 'native-base';
import {lock, enable, disable} from '../services/settings';

export default function Home() {
  return (
    <Box>
      <Button onPress={lock}>Lock</Button>
      <Button onPress={enable}>Enable</Button>
      <Button onPress={disable}>Disable</Button>
    </Box>
  );
}
