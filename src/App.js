/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';
import type {Node} from 'react';
import {NavigationContainer, DefaultTheme} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import {NativeBaseProvider, View} from 'native-base';
import {StatusBar} from 'react-native';

// ui screens
import Home from './screens/Home';
import Tutorial from './screens/Tutorial';

const Stack = createNativeStackNavigator();

const BlackTheme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    background: 'black',
  },
};

const App: () => Node = () => {
  return (
    <NativeBaseProvider>
      <StatusBar hidden />
      <NavigationContainer theme={BlackTheme}>
        <Stack.Navigator screenOptions={{headerShown: false}}>
          {/*<Stack.Screen name="Tutorial" component={Tutorial} />*/}
          <Stack.Screen name="Home" component={Home} />
        </Stack.Navigator>
      </NavigationContainer>
    </NativeBaseProvider>
  );
};

export default App;
