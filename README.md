# AutoFishing

A simple lightweight mod that automates the fishing process.

## Setup
- `/autofish true` will start the auto fishing mod. Do `/autofish false` to disable.
- `/delay (ticks)` is a command where you can set the delay of the rod being recasted. The default value is 20 ticks, or 1 second on a 20 tps server. 1 tick is equal to 50ms.
- `/inventorycheck true` will stop the auto fishing mod if a GUI opens. `/inventorycheck false` will keep the auto fishing mod running if a GUI opens. The default value is set to false.

## Key notes
- You need to hold a fishing rod in order to active auto fishing.
- Swapping from the fishing rod to a different item will automatically disable the auto fishing.
- Reeling in manually, your right mouse button, will automatically disable the auto fishing.
- The delay is reccomended to be set to 10-20 ticks.
- If you are using any form of a custom fishing rod, please use [this](https://github.com/quervyloll/AutoFish/tree/custom-fishing-rods) branch.
![example](https://github.com/quervyloll/AutoFish/blob/main/2024-10-04_03.48.42.png)

### Modrinth download
If you don't trust the build for some reaosn, there's a modrinth page you can download it from [here](https://modrinth.com/mod/auto-fish).
