Summary: A Scala FastAGI Application Container
Name: fastiga
Version: 0.1
Release: 1
License: Custom
Group: System/Devel
Source0: %{name}-%{version}.tar.gz
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-build
BuildRequires: scala
BuildRequires: javacc
Prereq: scala
Prereq: tomcat


%description
Fastiga is a FastAGI application container built with Scala.

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

/usr/share/tomcat5/common/lib/*.jar
/usr/share/tomcat5/webapps/*.war
