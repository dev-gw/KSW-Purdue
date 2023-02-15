## Machine Learning part   


## Dependencies   
```
Python 3.9.13
Anaconda 22.9.0
Spyder 5.2.2 (Editor)

tensorflow 2.10.0
scikit-learn 1.0.2
numpy 1.21.5
pandas 1.5.3
matplotlib 3.5.2
joblib 1.1.0
librosa 0.9.2
 - h5py 3.8.0
 - werkzeug 2.2.2
 - markdown 3.4.1
libsvm 3.3.0
```   

## To run this code
* Download Object Detection folder into your directory.
### Linux

```bash
# Install Anaconda
$ wget https://repo.anaconda.com/archive/Anaconda3-2022.10-Linux-x86_64.sh   
$ bash 'Anaconda3-2022.10-Linux-x86_64.sh'

# Run Anaconda
$ source ~/.bashrc
or 
$ . ~/.bashrc

# Make virtual environment   
conda create -n uavml python==3.9.13   
conda activate uavml   

# Run training code   
make env
make train
```

