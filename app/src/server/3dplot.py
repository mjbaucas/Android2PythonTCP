import matplotlib.pyplot as plt 
import matplotlib.ticker as mticker
from matplotlib import cm, colors
import numpy as np

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

chain_tick = np.arange(6)
chain_label = [1, 10, 100.0, 1000.0, 10000.0, 100000.0]

block_tick = np.arange(6)
block_label = [1, 2, 4, 8, 16, 32]

data = np.array([
    [100, 1, 8.20],
    [100, 2, 8.22],
    [100, 4, 9.09],
    [100, 8, 9.14],
    [100, 16, 9.32],
    [100, 32, 9.62],
    [1000, 1, 8.76],
    [1000, 2, 8.86],
    [1000, 4, 9.64],
    [1000, 8, 9.62],
    [1000, 16, 9.81],
    [1000, 32, 9.81],
    [10000, 1, 17.85],
    [10000, 2, 18.02],
    [10000, 4, 18.35],
    [10000, 8, 18.45],
    [10000, 16, 21.70],
    [10000, 32, 23.35],
    [100000, 1, 108.33],
    [100000, 2, 110.96],
    [100000, 4, 122.32],
    [100000, 8, 125.17],
    [100000, 16, 128.31],
    [100000, 32, 128.6],
])

x = data[:,1]
y = data[:,0]
z = data[:,2]


#def log_tick_formatter(val, pos=None):
#    return r"$10^{:.0f}$".format(val)

#ax.yaxis.set_major_formatter(mticker.FuncFormatter(log_tick_formatter))

surf = ax.plot_trisurf(np.log2(x), np.log10(y), z, cmap=cm.gist_earth, linewidth=0.2, antialiased = True)

plt.xticks(block_tick, block_label)

plt.yticks(chain_tick, chain_label)
plt.ylim(2, 5)


plt.colorbar(surf, ax = ax, shrink = 0.5, aspect = 5)

plt.savefig('process.png')
