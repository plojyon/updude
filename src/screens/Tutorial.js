import React, {useEffect} from 'react';
import {TextInput, ActivityIndicator, Vibration} from 'react-native';
import {
  Flex,
  Box,
  Center,
  View,
  Text as Ttext,
  ScrollView,
  Button,
} from 'native-base';
import Entypo from 'react-native-vector-icons/Entypo';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import {Title} from '../components/Text';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import Pressable from 'react-native/Libraries/Components/Pressable/Pressable';
import {
  startNFCread,
  startScan,
  stopNFCread,
  stopScan,
  startForegroundService,
  getSettings,
  saveDevice,
  wipeSettings,
} from '../services/settings';

const ChooseNFCScreen = ({navigation}) => {
  const [uuid, setUUID] = React.useState();
  const [friendlyName, setFriendlyName] = React.useState(
    slovenianNames[parseInt(Math.random() * slovenianNames.length)],
  );

  useEffect(() => {
    if (!uuid) {
      startNFCread(uuid1 => {
        console.log(`found ${uuid1}`);
        Vibration.vibrate(100);
        setUUID(uuid1);
        stopNFCread();
      });
    }
  }, [uuid]);

  return (
    <Flex
      flexDirection={'column'}
      justifyContent={'center'}
      alignItems="stretch"
      height={'100%'}>
      <Box
        //style={{borderColor: 'gray', borderWidth: 1}}
        borderRadius="5"
        width={'100%'}
        mb="7"
        flexGrow="0">
        <Center>
          <MaterialIcons
            name={'nfc'}
            size={100}
            style={{padding: 20}}
            color={main_color}
          />
        </Center>
        {!uuid && (
          <>
            <Text style={{textAlign: 'center'}} mb="10">
              Scan your NFC tag
            </Text>
            <ActivityIndicator />
          </>
        )}
        {uuid && (
          <>
            <Text style={{textAlign: 'center'}}>
              Found tag with UUID: {uuid}
            </Text>
            <Flex
              flexDirection={'row'}
              justifyContent="center"
              alignItems="center"
              mb="10">
              <Text fontSize={20}>Tag name: </Text>
              <TextInput
                style={{
                  color: main_color,
                  fontSize: 20,
                  fontWeight: 'bold',
                  borderBottomColor: main_color,
                  borderBottomWidth: 1,
                  textAlign: 'center',
                }}
                onChangeText={text => setFriendlyName(text)}
                value={friendlyName}
              />
            </Flex>
          </>
        )}
      </Box>
      {uuid && (
        <Flex flexDirection={'row'} justifyContent="space-evenly">
          <Box flexGrow={1}>
            <Pressable alignItems="center">
              <Entypo
                name="check"
                size={50}
                color="green"
                onPress={() => {
                  navigation.navigate('TutorialScreen', {
                    add_device: {type: 'nfc', uuid, name: friendlyName},
                  });
                }}
              />
              <Text>Save tag</Text>
            </Pressable>
          </Box>
          <Box flexGrow={1}>
            <Pressable alignItems="center">
              <Entypo
                name="cross"
                size={50}
                color="red"
                onPress={() => setUUID(null)}
              />

              <Text>Scan again</Text>
            </Pressable>
          </Box>
        </Flex>
      )}
    </Flex>
  );
};

const ChooseBLEScreen = ({navigation}) => {
  const [devices, setDevices] = React.useState(null);
  /*const [friendlyName, setFriendlyName] = React.useState(
    slovenianNames[parseInt(Math.random() * slovenianNames.length)],
  );*/

  const [isScanning, setIsScanning] = React.useState(false);

  useEffect(() => {
    if (!isScanning && !devices) {
      console.log('starting scan');
      setIsScanning(true);
      let devices_temp = [];
      const event_thing = startScan(devices_arr => {
        console.log('data got!');
        devices_temp = devices_arr
          .map(device => ({...device, type: 'ble'}))
          .sort((a, b) => (!!a.name ? -1 : 1));
      });

      setTimeout(() => {
        console.log('ended scan');
        stopScan();
        event_thing.remove();
        setDevices(devices_temp);
        setIsScanning(false);
      }, 3000);
    }
  }, [isScanning, devices]);

  return (
    <ScrollView>
      <View p="5">
        <Title>Pick your device</Title>
        {/*<Box
          mt={10}
          style={{borderColor: 'gray', borderWidth: 1}}
          borderRadius="5"
          width={'100%'}
          p="4">
          <Flex
            flexDirection={'row'}
            justifyContent="center"
            alignItems="center">
            <Text fontSize={20}>Bluetooth tag name: </Text>
            <TextInput
              style={{
                color: main_color,
                fontSize: 20,
                fontWeight: 'bold',
                borderBottomColor: main_color,
                borderBottomWidth: 1,
                textAlign: 'center',
              }}
              onChangeText={text => setFriendlyName(text)}
              value={friendlyName}
            />
          </Flex>
            </Box>*/}
        <Box
          mt={10}
          style={{borderColor: 'gray', borderWidth: 1}}
          borderRadius="5"
          width={'100%'}
          p="4">
          <Text fontSize="15" mb="10">
            Available devices
          </Text>
          {devices &&
            devices.map(device => (
              <View key={device.uuid}>
                <DeviceComponent
                  type={device.type}
                  name={device.name}
                  uuid={device.uuid}
                  custom_text_fnct={name => `Select ${name}`}
                  on_side_press={() => {
                    console.log('from plus, adding device', {
                      add_device: {
                        type: device.type,
                        uuid: device.uuid,
                        name: device.name || device.uuid,
                      },
                    });
                    navigation.navigate('TutorialScreen', {
                      add_device: {
                        type: device.type,
                        uuid: device.uuid,
                        name: device.name || device.uuid,
                      },
                    });
                  }}
                  sideIcon={<Entypo name={'plus'} size={20} color={'green'} />}
                />
              </View>
            ))}
          {devices && (
            <Pressable
              onPress={() => {
                setDevices(null);
              }}>
              <Text mt="5" textAlign="center">
                Scan again
              </Text>
            </Pressable>
          )}
          {!devices && (
            <View mb={10}>
              <ActivityIndicator />
            </View>
          )}
        </Box>
      </View>
    </ScrollView>
  );
};

const ChooseDeviceScreen = ({navigation}) => {
  const [type, setType] = React.useState();
  return (
    <View p="5">
      {!type && (
        <Flex flexDirection={'column'} alignItems="stretch" height={'100%'}>
          <Box
            //style={{borderColor: 'gray', borderWidth: 1}}
            borderRadius="5"
            width={'100%'}
            mb="7"
            flexGrow="0">
            <Title>Choose your device type</Title>
          </Box>
          <Flex flexGrow="1">
            <Flex
              flexGrow="1"
              padding={5}
              justifyContent="center"
              alignItems={'center'}>
              <Pressable
                onPress={() => navigation.navigate('ChooseBLEScreen')}
                alignItems={'center'}>
                <MaterialIcons
                  name={'bluetooth'}
                  size={100}
                  color={main_color}
                  style={{padding: 10}}
                />
                <Text style={{textAlign: 'center'}}>
                  When your phone detects your bluetooth tag in range, it will
                  unlock your device
                </Text>
              </Pressable>
            </Flex>
            <Flex
              flexGrow="1"
              padding={5}
              justifyContent="center"
              alignItems={'center'}>
              <Pressable
                onPress={() => navigation.navigate('ChooseNFCScreen')}
                alignItems={'center'}>
                <MaterialIcons
                  name={'nfc'}
                  size={100}
                  color={main_color}
                  style={{padding: 10}}
                />

                <Text style={{textAlign: 'center'}}>
                  Scan an NFC compliant tag to unlock your phone
                </Text>
              </Pressable>
            </Flex>
          </Flex>
        </Flex>
      )}
      {type == 'ble' && (
        <Flex flexDirection={'column'} alignItems="stretch" height={'100%'}>
          <Box
            //style={{borderColor: 'gray', borderWidth: 1}}
            borderRadius="5"
            width={'100%'}
            mb="7"
            flexGrow="0">
            <Title>Select your bluetooth device</Title>
          </Box>
        </Flex>
      )}
    </View>
  );
};

const Text = props => {
  return (
    <Ttext
      {...Object.assign({}, props, {
        style: Object.assign({color: 'white'}, props.style),
      })}
    />
  );
};

const main_color = '#00bcd4';

const DeviceComponent = ({
  type,
  name,
  uuid,
  on_side_press,
  sideIcon, // Icom component
  custom_text_fnct, // name -> text
}) => {
  const [confirmDel, setConfirmDel] = React.useState(false);

  if (!name && uuid) {
    name = uuid;
  }

  return (
    <Box padding="2">
      <Flex flexDirection="row" alignItems={'center'}>
        {(type == 'nfc' || type == 'ble') && (
          <MaterialIcons
            name={type == 'nfc' ? 'nfc' : 'bluetooth'}
            size={30}
            color={main_color}
            style={{padding: 5}}
          />
        )}
        {uuid && (
          <View flexGrow={1}>
            <Text flexGrow={1} style={{textAlign: 'left'}}>
              {confirmDel
                ? custom_text_fnct
                  ? custom_text_fnct(name)
                  : `Remove ${name}?`
                : name}
            </Text>
            <Text style={{color: 'gray'}} fontSize={10}>
              {uuid}
            </Text>
          </View>
        )}
        {!uuid && (
          <Text flexGrow={1} style={{textAlign: 'left'}}>
            {confirmDel
              ? custom_text_fnct
                ? custom_text_fnct(name)
                : `Remove ${name}?`
              : name}
          </Text>
        )}
        {confirmDel && (
          <>
            <Entypo
              name="check"
              size={30}
              color="green"
              onPress={() => {
                setConfirmDel(false);
                on_side_press && on_side_press();
              }}
              style={{paddingRight: 20}}
            />
            <Entypo
              name="cross"
              size={30}
              color="red"
              onPress={() => setConfirmDel(false)}
            />
          </>
        )}
        {!confirmDel && (
          <Pressable onPress={() => setConfirmDel(true)}>
            <View p="1">
              {sideIcon || <Entypo name={'minus'} size={20} color={'red'} />}
            </View>
          </Pressable>
        )}
        )
      </Flex>
    </Box>
  );
};

const AddDeviceComponent = ({onAdd}) => (
  <Button
    onPress={onAdd}
    mt={10}
    variant="outline"
    borderWidth={1}
    borderColor={'black'}>
    <Center padding="2">
      <Entypo name="plus" size={30} color={main_color} />
    </Center>
  </Button>
);

const Stack = createNativeStackNavigator();

export default () => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="TutorialScreen" component={TutorialScreen} />
    <Stack.Group screenOptions={{presentation: 'card'}}>
      <Stack.Screen
        name="AddDevice"
        component={ChooseDeviceScreen}
        animationEnabled={false}
      />
      <Stack.Screen name="ChooseNFCScreen" component={ChooseNFCScreen} />
      <Stack.Screen name="ChooseBLEScreen" component={ChooseBLEScreen} />
    </Stack.Group>
  </Stack.Navigator>
);

const TutorialScreen = ({route, navigation}) => {
  const [stepsNumber, setStepsNumber] = React.useState('200');
  const [devices, setDevices] = React.useState();

  useEffect(() => {
    console.log(devices);
    if (!devices) {
      getSettings(obj => {
        console.log('got settings');
        const ot = [];
        if ('ble' in obj) {
          if (obj['ble'] != null)
            ot.push({
              type: 'ble',
              name: obj['ble_name'],
              uuid: obj['ble'],
            });
        }
        if ('nfc' in obj) {
          if (obj['nfc'] != null) {
            ot.push({
              type: 'nfc',
              name: obj['nfc_name'],
              uuid: obj['nfc'],
            });
          }
        }
        if ('steps' in obj) {
          setStepsNumber(obj['steps']);
        }

        setDevices(ot);
      });
    }
    if (route.params && route.params.add_device) {
      if (
        devices.filter(({uuid}) => {
          return uuid == route.params.add_device.uuid;
        }).length == 0
      ) {
        console.log('added device');
        setDevices([...devices, route.params.add_device]);
      } else {
        console.log('Device already added');
      }
    }
  }, [route.params, devices]);

  const onTextChanged = text => {
    setStepsNumber(text);
  };

  return (
    <ScrollView>
      <Center p="5">
        <Box
          //style={{borderColor: 'gray', borderWidth: 1}}
          borderRadius="5"
          width={'100%'}
          p="4"
          mb="10">
          <Title>Welcome to wakelock</Title>
        </Box>
        <Box
          style={{borderColor: 'gray', borderWidth: 1}}
          borderRadius="5"
          width={'100%'}
          p="4">
          <Flex direction="row">
            <Text style={{color: 'white'}} flexShrink={1} fontSize={15}>
              How many steps do you want to take before considered awake?
            </Text>
            <TextInput
              style={{
                padding: 10,
                paddingLeft: 10,
                color: main_color,
                fontSize: 15,
                fontWeight: 'bold',
              }}
              keyboardType="numeric"
              onChangeText={text => onTextChanged(text)}
              value={stepsNumber}
            />
          </Flex>
        </Box>
        <Box
          style={{borderColor: 'gray', borderWidth: 1}}
          borderRadius="5"
          width={'100%'}
          p="4"
          mt={5}>
          <Text style={{color: 'white'}} flexShrink={1} fontSize={15} mb="10">
            List of devices that will unlock your phone
          </Text>
          {devices &&
            devices.map((dev, i) => (
              <DeviceComponent
                key={i}
                type={dev.type}
                name={dev.name}
                on_side_press={() => setDevices(devices.filter(x => x != dev))}
              />
            ))}
          <AddDeviceComponent onAdd={() => navigation.navigate('AddDevice')} />
        </Box>
        <Box borderRadius="5" width={'100%'} mt={5}>
          <Button
            onPress={() => {
              console.log('wiping settings');
              wipeSettings(() => {
                console.log('Saving settings');

                devices.forEach(dev => {
                  saveDevice(dev);
                });
              });
            }}>
            <Text>Save settings</Text>
          </Button>
        </Box>
      </Center>
    </ScrollView>
  );
};

const slovenianNames = [
  'Mo??ko',
  'Franc',
  'Janez',
  'Marko',
  'Ivan',
  'Anton',
  'Andrej',
  'Jo??ef',
  'Jo??e',
  'Luka',
  'Peter',
  'Marjan',
  'Matej',
  'Toma??',
  'Milan',
  'Ale??',
  'Branko',
  'Bojan',
  'Robert',
  'Rok',
  'Bo??tjan',
  'Matja??',
  'Gregor',
  'Miha',
  'Stanislav',
  'Martin',
  'David',
  'Igor',
  'Jan',
  'Dejan',
  'Boris',
  'Du??an',
  'Nejc',
  '??iga',
  'Jure',
  'Uro??',
  'Alojz',
  'Bla??',
  '??an',
  'Mitja',
  'Simon',
  'Matic',
  'Klemen',
  'Darko',
  'Primo??',
  'Jernej',
  'An??e',
  'Ga??per',
  'Drago',
  'Aleksander',
  'Jaka',
  'Jakob',
  'Alja??',
  'Miran',
  'Tadej',
  'Denis',
  'Roman',
  'Nik',
  '??tefan',
  'Vladimir',
  'Damjan',
  'Matija',
  'Borut',
  'Sre??ko',
  'Slavko',
  'Filip',
  'Janko',
  'Tilen',
  'Zoran',
  'Mirko',
  'Alen',
  'Miroslav',
  'Domen',
  'Vid',
  'Danijel',
  'Goran',
  'Mark',
  'Tim',
  'Stanko',
  'Mihael',
  'Leon',
  'Matev??',
  'Urban',
  'Sa??o',
  'Jurij',
  'Andra??',
  'Iztok',
  'Marijan',
  'Vinko',
  'Dragan',
  'Alojzij',
  'Maks',
  'Viktor',
  'Benjamin',
  'Erik',
  'Lovro',
  'Zvonko',
  'Samo',
  'Gal',
  'Zdravko',
  'Rudolf',
];
