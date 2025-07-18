# react-native-opticon-scanner


React Native bridge for Opticon H-31 hardware barcode scanner SDK. Enables high-speed QR and barcode scanning using the device's built-in scanner hardware.



## Installation

```sh
npm install react-native-opticon-scanner
```

## Usage



```tsx
import STMOpticonScanner from 'react-native-opticon-scanner';
import { DeviceEventEmitter } from 'react-native';

// Initialize scanner when your screen is focused
await STMOpticonScanner.initializeScanner();

// Start a scan
await STMOpticonScanner.startScan();

// Stop a scan
await STMOpticonScanner.stopScan();

// Start trigger mode (continuous scan)
await STMOpticonScanner.startTrigger();

// Stop trigger mode
await STMOpticonScanner.stopTrigger();

// Listen for scan results (DeviceEventEmitter is required)
DeviceEventEmitter.addListener('onScannerResult', (data) => {
  console.log('Barcode:', data.barcode, 'CodeId:', data.codeId);
});
```


## API

- `initializeScanner()`: Initializes the scanner (returns a string message).
- `startScan()`: Starts scanning (returns a string message).
- `stopScan()`: Stops scanning (returns a string message).
- `startTrigger()`: Starts trigger mode (returns a string message).
- `stopTrigger()`: Stops trigger mode (returns a string message).


### Events
- `onScannerResult`: Emitted via `DeviceEventEmitter` with `{ barcode, codeId }` when a scan is completed. This is the only way to receive scan results in JavaScript.


## License

MIT
