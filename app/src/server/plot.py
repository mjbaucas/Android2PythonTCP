import matplotlib.pyplot as plt 
import numpy as np

lg_nexus5 = [7.64, 8.27, 8.83, 16.90, 25.32, 41.40]
samsung_watch4 = [10.42, 12.91, 16.08, 26.60, 39.56, 63.96]
rpi_3Bp = [8.82, 11.50, 13.16, 22.51, 34.67, 71.94]

distance_tick = np.arange(6)
distance_label = [1024, 2048, 4096, 8192, 16384, 32768]

plt.figure(0)
plt.plot(distance_tick, lg_nexus5, marker='o', label="LG Nexus 5")
plt.plot(distance_tick, samsung_watch4, marker='o', label="Samsung Galaxy Watch 4")
plt.plot(distance_tick, rpi_3Bp, marker='o', label="Raspberry Pi 3B+")
plt.legend()
plt.xticks(distance_tick, distance_label)
plt.grid(linestyle = '--', linewidth = 0.5)
plt.ylabel("Latency (ms)")
plt.xlabel("Packet Size (KB)")
plt.savefig('latdev.png')
