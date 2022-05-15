import React from 'react';
import {Text as BoringText, Box} from 'native-base';

export const Text = ({children}) => (
  <BoringText
    style={{
      color: 'white',
      fontSize: 20,
    }}>
    {children}
  </BoringText>
);

export const Title = ({children}) => (
  <Box
    _text={{
      fontSize: '50',
      fontWeight: 'bold',
      color: 'warmGray.50',
    }}>
    {children}
  </Box>
);
