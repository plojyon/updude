import React from 'react';
import {Box, Button} from 'native-base';
import {hello} from '../services/settings';

export default function Home() {
  return (
    <Box>
      <Button onPress={hello}>Hello World!</Button>
    </Box>
  );
}
