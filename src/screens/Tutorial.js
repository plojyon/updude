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
import {startScan, stopScan} from '../services/settings';

const ChooseNFCScreen = ({navigation}) => {
  const [uuid, setUUID] = React.useState();
  const [friendlyName, setFriendlyName] = React.useState(
    slovenianNames[parseInt(Math.random() * slovenianNames.length)],
  );

  useEffect(() => {
    setTimeout(() => {
      // set rfid uuid
      Vibration.vibrate(100);
      setUUID('98273403324');
    }, 5000);
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
  const [friendlyName, setFriendlyName] = React.useState(
    slovenianNames[parseInt(Math.random() * slovenianNames.length)],
  );

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
      }, 5000);
    }
  }, [isScanning, devices]);

  return (
    <ScrollView>
      <View p="5">
        <Title>Pick your device</Title>
        <Box
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
        </Box>
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
                        name: friendlyName,
                      },
                    });
                    navigation.navigate('TutorialScreen', {
                      add_device: {
                        type: device.type,
                        uuid: device.uuid,
                        name: friendlyName,
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
  const [devices, setDevices] = React.useState([
    {type: 'ble', uuid: '2', name: 'Coffee machine'},
    {type: 'nfc', uuid: '6', name: 'Table tag'},
  ]);

  useEffect(() => {
    if (route.params && route.params.add_device) {
      console.log(route.params.add_device);
      if (
        devices.filter(({uuid}) => {
          console.log(uuid, route.params.add_device.uuid);
          return uuid == route.params.add_device.uuid;
        }).length == 0
      ) {
        console.log('added device');
        setDevices([...devices, route.params.add_device]);
      } else {
        console.log('Device already added');
      }
    }
  }, [route.params]);

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
          <Title>Welcome to updude</Title>
        </Box>
        <Box
          style={{borderColor: 'gray', borderWidth: 1}}
          borderRadius="5"
          width={'100%'}
          p="4">
          <Flex direction="row">
            <Text style={{color: 'white'}} flexShrink={1} fontSize={15}>
              How many steps do you want to take today?
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
          {devices.map((dev, i) => (
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
              console.log('ok');
            }}>
            <Text>Save settings</Text>
          </Button>
        </Box>
      </Center>
    </ScrollView>
  );
};

const slovenianNames = [
  'Moško',
  'Franc',
  'Janez',
  'Marko',
  'Ivan',
  'Anton',
  'Andrej',
  'Jožef',
  'Jože',
  'Luka',
  'Peter',
  'Marjan',
  'Matej',
  'Tomaž',
  'Milan',
  'Aleš',
  'Branko',
  'Bojan',
  'Robert',
  'Rok',
  'Boštjan',
  'Matjaž',
  'Gregor',
  'Miha',
  'Stanislav',
  'Martin',
  'David',
  'Igor',
  'Jan',
  'Dejan',
  'Boris',
  'Dušan',
  'Nejc',
  'Žiga',
  'Jure',
  'Uroš',
  'Alojz',
  'Blaž',
  'Žan',
  'Mitja',
  'Simon',
  'Matic',
  'Klemen',
  'Darko',
  'Primož',
  'Jernej',
  'Anže',
  'Gašper',
  'Drago',
  'Aleksander',
  'Jaka',
  'Jakob',
  'Aljaž',
  'Miran',
  'Tadej',
  'Denis',
  'Roman',
  'Nik',
  'Štefan',
  'Vladimir',
  'Damjan',
  'Matija',
  'Borut',
  'Srečko',
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
  'Matevž',
  'Urban',
  'Sašo',
  'Jurij',
  'Andraž',
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
