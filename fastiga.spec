Summary: The konfirmagi library
Name: konfirmagi
Version: 0.1
Release: 1
License: Custom
Group: System/Devel
Source0: %{name}-%{version}.tar.gz
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-build
BuildRequires: weaver
#Prereq: 


%description
konfirmagi is a library for ...

%prep
%setup -q -n %{name}-%{version}

%build
make

%install
make DESTDIR=%buildroot install

%clean
rm -rf ${RPM_BUILD_ROOT}

%files
%defattr(-,root,root)

/usr/share/tomcat5/common/lib/konfirmagi*
